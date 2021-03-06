package com.example.flashcardapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    FlashcardDatabase flashcardDatabase;
    List<Flashcard> allFlashcards;
    Flashcard cardToEdit;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flashcardDatabase = new FlashcardDatabase(getApplicationContext());
        allFlashcards = flashcardDatabase.getAllCards();


        // Get the question and answer TextViews and set the answer TextView to invisible on app launch.
        TextView flashCardQuestionTextView = findViewById(R.id.mainFlashCardQuestionTextView);
        TextView flashCardAnswerTextView = findViewById(R.id.mainFlashCardAnswerTextView);

        // Getting the TextViews of each choice into variables
        TextView answerChoiceOneTextView = findViewById(R.id.answerChoiceOneTextView);
        TextView answerChoiceTwoTextView = findViewById(R.id.answerChoiceTwoTextView);
        TextView answerChoiceThreeTextView = findViewById(R.id.answerChoiceThreeTextView);


        //Make the answer invisible by default and show the question on top of it instead.
        flashCardAnswerTextView.setVisibility(View.INVISIBLE);

        if (allFlashcards != null && allFlashcards.size() > 0) {
            flashCardQuestionTextView.setText(allFlashcards.get(0).getQuestion());
            flashCardAnswerTextView.setText(allFlashcards.get(0).getAnswer());
            shuffleAnswers(allFlashcards.get(0).getAnswer(), allFlashcards.get(0).getWrongAnswer1(), allFlashcards.get(0).getWrongAnswer2());
            startTimer();
        }

        // The Question and Answer flash cards Section
        flashCardQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashCardQuestionTextView.setCameraDistance(25000);
                flashCardAnswerTextView.setCameraDistance(25000);
                flashCardQuestionTextView.animate()
                        .rotationY(90)
                        .setDuration(200)
                        .withEndAction(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        flashCardQuestionTextView.setVisibility(View.INVISIBLE);
                                        flashCardAnswerTextView.setVisibility(View.VISIBLE);
                                        // second quarter turn
                                        flashCardAnswerTextView.setRotationY(-90);
                                        flashCardAnswerTextView.animate()
                                                .rotationY(0)
                                                .setDuration(200)
                                                .start();

                                    }
                                }
                        ).start();
            }
        });

        flashCardAnswerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the answer card to invisible and set the question card to visible when clicked
                flashCardQuestionTextView.setCameraDistance(25000);
                flashCardAnswerTextView.setCameraDistance(25000);
                flashCardAnswerTextView.animate()
                        .rotationY(90)
                        .setDuration(200)
                        .withEndAction(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        flashCardAnswerTextView.setVisibility(View.INVISIBLE);
                                        flashCardQuestionTextView.setVisibility(View.VISIBLE);
                                        // second quarter turn
                                        flashCardQuestionTextView.setRotationY(-90);
                                        flashCardQuestionTextView.animate()
                                                .rotationY(0)
                                                .setDuration(200)
                                                .start();
                                    }
                                }
                        ).start();
            }
        });

// The multiple choice section

        answerChoiceOneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answerString = flashCardAnswerTextView.getText().toString();
                if (!answerString.equals("")) {
                    Answers answers = getCorrectAndWrongAnswers(answerString, answerChoiceOneTextView, answerChoiceTwoTextView, answerChoiceThreeTextView);
                    changeAnswerColor(answerChoiceOneTextView, answers.getCorrectAnswerTextView());
                }
            }
        });
        answerChoiceTwoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answerString = flashCardAnswerTextView.getText().toString();
                if (!answerString.equals("")) {
                    Answers answers = getCorrectAndWrongAnswers(answerString, answerChoiceOneTextView, answerChoiceTwoTextView, answerChoiceThreeTextView);
                    changeAnswerColor(answerChoiceTwoTextView, answers.getCorrectAnswerTextView());
                }
            }
        });
        answerChoiceThreeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answerString = flashCardAnswerTextView.getText().toString();
                if (!answerString.equals("")) {
                    Answers answers = getCorrectAndWrongAnswers(answerString, answerChoiceOneTextView, answerChoiceTwoTextView, answerChoiceThreeTextView);
                    changeAnswerColor(answerChoiceThreeTextView, answers.getCorrectAnswerTextView());
                }
            }
        });

        // The hide or show icon
        ImageView visibilityImage = findViewById(R.id.choiceVisibilityChangerIcon);
        final boolean[] choicesAreHidden = {false};
        visibilityImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make all choices invisible
                if (!choicesAreHidden[0]) {
                    answerChoiceTwoTextView.setVisibility(View.INVISIBLE);
                    answerChoiceOneTextView.setVisibility(View.INVISIBLE);
                    answerChoiceThreeTextView.setVisibility(View.INVISIBLE);

                    // Change the image to one that indicates all choices are blocked (Slashed Eye)
                    visibilityImage.setImageResource(R.drawable.ic_slashed_eye);
                    choicesAreHidden[0] = true;
                } else if (choicesAreHidden[0]) {
                    answerChoiceTwoTextView.setVisibility(View.VISIBLE);
                    answerChoiceOneTextView.setVisibility(View.VISIBLE);
                    answerChoiceThreeTextView.setVisibility(View.VISIBLE);

                    // Change the image to one that indicates all choices are shown (Normal Eye)
                    visibilityImage.setImageResource(R.drawable.ic_eye);
                    choicesAreHidden[0] = false;
                }
            }
        });

        // Sets all colors to default when the background of the screen is clicked
        findViewById(R.id.refreshIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAnswerChoiceColors();
            }
        });

        // The add a new flashcard question icon
        findViewById(R.id.addCardQuestionsIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creates a new intent and starts an activity with the expectation of a result being returned from the started activity.
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                MainActivity.this.startActivityForResult(intent, 100);
                overridePendingTransition(R.anim.right_to_center, R.anim.center_to_left);
            }
        });

        findViewById(R.id.editCurrentCardsIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allFlashcards.size() > 0) {
                    for (Flashcard card : allFlashcards) {
                        if (card.getQuestion().equals(flashCardQuestionTextView.getText().toString())) {
                            cardToEdit = card;
                        }
                    }
                }
                String currentQuestionString = flashCardQuestionTextView.getText().toString();
                String currentCorrectAnswerString = flashCardAnswerTextView.getText().toString();
                if (currentQuestionString.equals("--") || currentCorrectAnswerString.equals("--")) {
                    return;
                }
                Answers updatedAnswers = getCorrectAndWrongAnswers(currentCorrectAnswerString, answerChoiceOneTextView, answerChoiceTwoTextView, answerChoiceThreeTextView);
                String currentWrongAnswerChoiceOneString = updatedAnswers.getWrongAnswerTextViews().get(0).getText().toString();
                String currentWrongAnswerChoiceTwoString = updatedAnswers.getWrongAnswerTextViews().get(1).getText().toString();

                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                intent.putExtra("currentQuestionString", currentQuestionString);
                intent.putExtra("currentCorrectAnswerString", currentCorrectAnswerString);
                intent.putExtra("currentWrongAnswerChoiceOneString", currentWrongAnswerChoiceOneString);
                intent.putExtra("currentWrongAnswerChoiceTwoString", currentWrongAnswerChoiceTwoString);
                MainActivity.this.startActivityForResult(intent, 101);
                MainActivity.this.overridePendingTransition(R.anim.right_to_center, R.anim.center_to_left);
            }
        });


        findViewById(R.id.nextFlashCardIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allFlashcards.size() > 0) {
                    // make sure we don't get an IndexOutOfBoundsError if we are viewing the last indexed card in our list
                    // set the question and answer TextViews with data from the database
                    allFlashcards = flashcardDatabase.getAllCards();
                    if (allFlashcards.size() > 1) {
                        final Animation centerToLeftAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.center_to_left);
                        final Animation rightToCenterAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.right_to_center);
                        if (flashCardAnswerTextView.getVisibility() == View.VISIBLE) {
                            flashCardAnswerTextView.startAnimation(centerToLeftAnim);
                        } else {
                            flashCardQuestionTextView.startAnimation(centerToLeftAnim);
                        }

                        if (answerChoiceOneTextView.getVisibility() == View.VISIBLE) {
                            answerChoiceOneTextView.startAnimation(centerToLeftAnim);
                            answerChoiceTwoTextView.startAnimation(centerToLeftAnim);
                            answerChoiceThreeTextView.startAnimation(centerToLeftAnim);
                        }

                        centerToLeftAnim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                flashCardQuestionTextView.setRotationY(0);
                                flashCardAnswerTextView.setVisibility(View.INVISIBLE);
                                flashCardQuestionTextView.setVisibility(View.VISIBLE);
                                flashCardQuestionTextView.startAnimation(rightToCenterAnim);
                                if (answerChoiceOneTextView.getVisibility() == View.VISIBLE) {
                                    answerChoiceOneTextView.startAnimation(rightToCenterAnim);
                                    answerChoiceTwoTextView.startAnimation(rightToCenterAnim);
                                    answerChoiceThreeTextView.startAnimation(rightToCenterAnim);
                                }
                                Flashcard flashcard = allFlashcards.get(getRandomNumber(0, allFlashcards.size() - 1));
                                ((TextView) findViewById(R.id.mainFlashCardQuestionTextView)).setText(flashcard.getQuestion());
                                ((TextView) findViewById(R.id.mainFlashCardAnswerTextView)).setText(flashcard.getAnswer());
                                shuffleAnswers(flashcard.getAnswer(), flashcard.getWrongAnswer1(), flashcard.getWrongAnswer2());
                                resetAnswerChoiceColors();
                                restartTimer();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                    }
                }
            }
        });

        findViewById(R.id.deleteFlashCardIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allFlashcards.size() > 0) {
                    if (allFlashcards.size() == 1) {
                        flashcardDatabase.deleteCard(((TextView) findViewById(R.id.mainFlashCardQuestionTextView)).getText().toString());
                        ((TextView) findViewById(R.id.mainFlashCardQuestionTextView)).setText("--");
                        ((TextView) findViewById(R.id.mainFlashCardAnswerTextView)).setText("--");
                        ((TextView) findViewById(R.id.answerChoiceOneTextView)).setText("-");
                        ((TextView) findViewById(R.id.answerChoiceTwoTextView)).setText("-");
                        ((TextView) findViewById(R.id.answerChoiceThreeTextView)).setText("-");
                        countDownTimer.cancel();
                        return;
                    }
                    final Animation centerToLeftAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.center_to_left);
                    final Animation rightToCenterAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.right_to_center);

                    if (flashCardAnswerTextView.getVisibility() == View.VISIBLE) {
                        flashCardAnswerTextView.startAnimation(centerToLeftAnim);
                    } else {
                        flashCardQuestionTextView.startAnimation(centerToLeftAnim);
                    }

                    if (answerChoiceOneTextView.getVisibility() == View.VISIBLE) {
                        answerChoiceOneTextView.startAnimation(centerToLeftAnim);
                        answerChoiceTwoTextView.startAnimation(centerToLeftAnim);
                        answerChoiceThreeTextView.startAnimation(centerToLeftAnim);
                    }

                    centerToLeftAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            flashCardQuestionTextView.setRotationY(0);
                            flashCardAnswerTextView.setVisibility(View.INVISIBLE);
                            flashCardQuestionTextView.setVisibility(View.VISIBLE);
                            flashCardQuestionTextView.startAnimation(rightToCenterAnim);
                            if (answerChoiceOneTextView.getVisibility() == View.VISIBLE) {
                                answerChoiceOneTextView.startAnimation(rightToCenterAnim);
                                answerChoiceTwoTextView.startAnimation(rightToCenterAnim);
                                answerChoiceThreeTextView.startAnimation(rightToCenterAnim);
                            }
                            flashcardDatabase.deleteCard(((TextView) findViewById(R.id.mainFlashCardQuestionTextView)).getText().toString());
                            allFlashcards = flashcardDatabase.getAllCards();
                            Flashcard flashcard;
                            if (allFlashcards.size() == 1){
                                flashcard = allFlashcards.get(0);
                            } else {
                                flashcard = allFlashcards.get(getRandomNumber(0, allFlashcards.size() - 1));
                            }

                            ((TextView) findViewById(R.id.mainFlashCardQuestionTextView)).setText(flashcard.getQuestion());
                            ((TextView) findViewById(R.id.mainFlashCardAnswerTextView)).setText(flashcard.getAnswer());
                            shuffleAnswers(flashcard.getAnswer(), flashcard.getWrongAnswer1(), flashcard.getWrongAnswer2());
                            resetAnswerChoiceColors();
                            restartTimer();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
            }
        });
    }

    private void restartTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(16000, 1000) {
            public void onTick(long millisUntilFinished) {
                ((TextView) findViewById(R.id.timerTextView)).setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
            }
        };
        countDownTimer.start();
    }

    void resetAnswerChoiceColors() {
        findViewById(R.id.answerChoiceOneTextView).setBackgroundResource(R.drawable.answer_card_background);
        findViewById(R.id.answerChoiceTwoTextView).setBackgroundResource(R.drawable.answer_card_background);
        findViewById(R.id.answerChoiceThreeTextView).setBackgroundResource(R.drawable.answer_card_background);
    }

    void changeAnswerColor(TextView clickedAnswerChoice, TextView correctAnswerChoice) {
        if (clickedAnswerChoice.equals(correctAnswerChoice)) {
            clickedAnswerChoice.setBackgroundResource(R.drawable.right_answer_background);
            new ParticleSystem(MainActivity.this, 100, R.drawable.confetti_particle, 3000)
                    .setSpeedRange(0.2f, 0.5f)
                    .oneShot(clickedAnswerChoice, 100);
        } else {
            clickedAnswerChoice.setBackgroundResource(R.drawable.wrong_answer_background);
            correctAnswerChoice.setBackgroundResource(R.drawable.right_answer_background);
        }
    }

    // This function is just something I wanted to implement. It shuffles the answer choices to make the app a bit more effective for studying.
    void shuffleAnswers(String answerChoiceOne, String answerChoiceTwo, String answerChoiceThree) {
        Map<Integer, String[]> shufflePatterns = new HashMap<Integer, String[]>();
        // These might look complex, but it's just some hard-coded patterns for the multiple choice.
        shufflePatterns.put(1, new String[]{answerChoiceOne, answerChoiceTwo, answerChoiceThree});
        shufflePatterns.put(2, new String[]{answerChoiceOne, answerChoiceThree, answerChoiceTwo});
        shufflePatterns.put(3, new String[]{answerChoiceTwo, answerChoiceOne, answerChoiceThree});
        shufflePatterns.put(4, new String[]{answerChoiceTwo, answerChoiceThree, answerChoiceOne});
        shufflePatterns.put(5, new String[]{answerChoiceThree, answerChoiceOne, answerChoiceTwo});
        shufflePatterns.put(6, new String[]{answerChoiceThree, answerChoiceTwo, answerChoiceOne});
        int randomNumber = (int) Math.floor(Math.random() * 6 + 1);

        ((TextView) findViewById(R.id.answerChoiceOneTextView)).setText(Objects.requireNonNull(shufflePatterns.get(randomNumber))[0]);
        ((TextView) findViewById(R.id.answerChoiceTwoTextView)).setText(Objects.requireNonNull(shufflePatterns.get(randomNumber))[1]);
        ((TextView) findViewById(R.id.answerChoiceThreeTextView)).setText(Objects.requireNonNull(shufflePatterns.get(randomNumber))[2]);
    }

    // Answers class to help with returning two values from a function: The correct answer and an array list of the wrong answers.
    public static class Answers {
        public TextView correctAnswerTextView;
        public ArrayList<TextView> wrongAnswerTextViews;

        Answers(TextView correctAnswerTextView, ArrayList<TextView> wrongAnswerTextViews) {
            this.correctAnswerTextView = correctAnswerTextView;
            this.wrongAnswerTextViews = wrongAnswerTextViews;
        }

        public TextView getCorrectAnswerTextView() {
            return correctAnswerTextView;
        }

        public ArrayList<TextView> getWrongAnswerTextViews() {
            return wrongAnswerTextViews;
        }
    }

    // The function used to get the right and wrong answers. It returns an Object of the Answers Class.
    Answers getCorrectAndWrongAnswers(String answerString, TextView answerChoiceOneTextView, TextView answerChoiceTwoTextView, TextView answerChoiceThreeTextView) {

        TextView[] answerChoiceTextViews = {answerChoiceOneTextView, answerChoiceTwoTextView, answerChoiceThreeTextView};
        ArrayList<TextView> wrongAnswerChoiceTextViews = new ArrayList<TextView>();
        TextView correctChoiceTextView = answerChoiceOneTextView;

        for (TextView x : answerChoiceTextViews) {
            if (x.getText().toString().equals(answerString)) {
                correctChoiceTextView = x;
            } else {
                wrongAnswerChoiceTextViews.add(x);
            }
        }
        return new Answers(correctChoiceTextView, wrongAnswerChoiceTextViews);
    }

    public int getRandomNumber(int minNumber, int maxNumber) {
        Random rand = new Random();
        return rand.nextInt((maxNumber - minNumber) + 1) + minNumber;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Basic error checking and activity authentication is done in the condition of this if statement.
        // We check if the request code is equal to the one assigned when the add card icon is clicked
        // We also make sure to run the body of the if statement if the data returned is not null, so we avoid a crash.
        if ((requestCode == 100 || requestCode == 101) && data != null) {
            // We get the text for the new question and for the new correct answer for the question
            String newQuestionString = data.getExtras().getString("newQuestionString");
            String newCorrectAnswerString = data.getExtras().getString("newCorrectAnswerString");
            String wrongAnswerOneString = data.getExtras().getString("wrongAnswerOneString");
            String wrongAnswerTwoString = data.getExtras().getString("wrongAnswerTwoString");

            // We target the original question and answer TextViews and set the text to the new ones we received above.
            ((TextView) findViewById(R.id.mainFlashCardQuestionTextView)).setText(newQuestionString);
            ((TextView) findViewById(R.id.mainFlashCardAnswerTextView)).setText(newCorrectAnswerString);
            if (requestCode == 100) {
                flashcardDatabase.insertCard(new Flashcard(newQuestionString, newCorrectAnswerString, wrongAnswerOneString, wrongAnswerTwoString));

            } else if (requestCode == 101) {
                cardToEdit.setQuestion(newQuestionString);
                cardToEdit.setAnswer(newCorrectAnswerString);
                cardToEdit.setWrongAnswer1(wrongAnswerOneString);
                cardToEdit.setWrongAnswer2(wrongAnswerTwoString);
                flashcardDatabase.updateCard(cardToEdit);
            }
            allFlashcards = flashcardDatabase.getAllCards();
            shuffleAnswers(newCorrectAnswerString, wrongAnswerOneString, wrongAnswerTwoString);
            resetAnswerChoiceColors();
            Snackbar.make(findViewById(R.id.mainFlashCardQuestionTextView), "Updated...", Snackbar.LENGTH_SHORT).show();
            if (countDownTimer == null) {
                startTimer();
            } else {
                restartTimer();
            }
            // TODO: 3/7/2021 Update the rest of the choices accordingly
        }
    }
}